package meccano.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.FileConfig;
import org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.tomcat.TomcatCopyingInstalledLocalDeployer;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.codehaus.cargo.util.log.FileLogger;
import org.codehaus.cargo.util.log.LogLevel;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test.
 * Deploy bridge and composite wars and check coordination.
 *
 * @author Thierry Uriot
 */
public class IntegrationIT {

  /** logger. */
  private static final Logger LOG = Logger.getLogger(IntegrationIT.class.getName());
  
  private static String bridgeWarPath;
  private static String test1ClientWarPath;
  private static String test1CommonWarPath;
  private static String test1ServiceWarPath;
  private static String test1ServiceV2WarPath;
  private static String test2ClientWarPath;
  private static String test2CommonWarPath;
  private static String test2ServiceWarPath;
  private static String test3ClientWarPath;
  private static String test3CommonWarPath;
  private static String test3ServiceWarPath;
  private static int tempo;
  private static InstalledLocalContainer container;
  private static TomcatCopyingInstalledLocalDeployer deployer;
  private static DeployableFactory factory;

  /**
   * Start tomcat before tests (and deploy bridge).
   */
  @BeforeClass
  public static void before() {
    String tomcatVersion = System.getProperty("tomcat.version");
    Tomcat tomcat = null;
    try {
      tomcat = Tomcat.valueOf(tomcatVersion);
    }
    catch(IllegalArgumentException e) {
      Assert.fail("Unknown tomcat version (TOMCAT5,TOMCAT6,TOMCAT7)");
    }
    Boolean tomcatDownload = Boolean.valueOf(System.getProperty("tomcat.download"));
    String tomcatDownloadDir = System.getProperty("tomcat.download.dir");
    String tomcatHome = System.getProperty("tomcat.home");
    String baseDir = System.getProperty("basedir");
    String jdkHome = System.getProperty("jdk.home");
    bridgeWarPath = System.getProperty("bridge.war.path");
    test1ClientWarPath = System.getProperty("test1.client.war.path");
    test1CommonWarPath = System.getProperty("test1.common.war.path");
    test1ServiceWarPath = System.getProperty("test1.service.war.path");
    test1ServiceV2WarPath = System.getProperty("test1.servicev2.war.path");
    test2ClientWarPath = System.getProperty("test2.client.war.path");
    test2CommonWarPath = System.getProperty("test2.common.war.path");
    test2ServiceWarPath = System.getProperty("test2.service.war.path");
    test3ClientWarPath = System.getProperty("test3.client.war.path");
    test3CommonWarPath = System.getProperty("test3.common.war.path");
    test3ServiceWarPath = System.getProperty("test3.service.war.path");
    tempo = Integer.parseInt(System.getProperty("tempo"));
    LOG.log(Level.INFO, "tomcat version            = {0} ({1})", new Object[]{tomcatVersion, tomcat.version});
    LOG.log(Level.INFO, "tomcat download           = {0}", tomcatDownload);
    if (tomcatDownload) {
      LOG.log(Level.INFO, "tomcat download dir     = {0}", tomcatDownloadDir);
    }
    if (!tomcatDownload) {
      LOG.log(Level.INFO, "tomcat home             = {0}", tomcatHome);
    }
    LOG.log(Level.INFO, "project home              = {0}", baseDir);
    LOG.log(Level.INFO, "java home                 = {0}", jdkHome);
    LOG.log(Level.INFO, "bridge war path           = {0}", bridgeWarPath);
    LOG.log(Level.INFO, "test1 client war path     = {0}", test1ClientWarPath);
    LOG.log(Level.INFO, "test1 common war path     = {0}", test1CommonWarPath);
    LOG.log(Level.INFO, "test1 service war path    = {0}", test1ServiceWarPath);
    LOG.log(Level.INFO, "test1 service v2 war path = {0}", test1ServiceV2WarPath);
    LOG.log(Level.INFO, "test2 client war path     = {0}", test2ClientWarPath);
    LOG.log(Level.INFO, "test2 common war path     = {0}", test2CommonWarPath);
    LOG.log(Level.INFO, "test2 service war path    = {0}", test2ServiceWarPath);
    LOG.log(Level.INFO, "test3 client war path     = {0}", test3ClientWarPath);
    LOG.log(Level.INFO, "test3 common war path     = {0}", test3CommonWarPath);
    LOG.log(Level.INFO, "test3 service war path    = {0}", test3ServiceWarPath);
    LOG.log(Level.INFO, "tempo                     = {0}", tempo);

    if (tomcatDownload) {
      LOG.log(Level.INFO, "downloading tomcat");
      try {
        ZipURLInstaller installer = new ZipURLInstaller(new URL(tomcat.downloadURL));
        installer.setExtractDir(tomcatDownloadDir);
        installer.install();
        tomcatHome=installer.getHome();
        LOG.log(Level.INFO, "tomcat home               = {0}", tomcatHome);
      }
      catch(MalformedURLException e) {
        Assert.fail("url not found");
      }
    }
    
    StandaloneLocalConfiguration configuration = (StandaloneLocalConfiguration) new DefaultConfigurationFactory()
        .createConfiguration(tomcat.version, ContainerType.INSTALLED, ConfigurationType.STANDALONE, tomcatHome + "/cargo");
    FileConfig serverConfigXml = new FileConfig();
    serverConfigXml.setFile(baseDir + "/src/test/resources/server" + tomcatVersion + ".xml"); // set backgroundProcessorDelay to speedup test
    serverConfigXml.setToDir("conf");
    serverConfigXml.setToFile("server.xml");
    serverConfigXml.setOverwrite(true);
    configuration.setConfigFileProperty(serverConfigXml);
    configuration.setProperty(GeneralPropertySet.JAVA_HOME, jdkHome);
    
    container = (InstalledLocalContainer) new DefaultContainerFactory()
      .createContainer(tomcat.version, ContainerType.INSTALLED, configuration);
    container.setHome(tomcatHome);
    
    final FileLogger logger = new FileLogger(baseDir + "/target/cargo.log", false);
	  logger.setLevel(LogLevel.DEBUG);
	  configuration.setLogger(logger);
	  container.setLogger(logger);
    container.setOutput(baseDir + "/target/tomcat.log");
    container.setAppend(false);

	  LOG.info("starting tomcat");
    container.start();
    
    deployer = new TomcatCopyingInstalledLocalDeployer(container);
    factory = new DefaultDeployableFactory();
    WAR bridgeWar      = (WAR) factory.createDeployable(container.getId(), bridgeWarPath,      DeployableType.WAR);
    deployer.deploy(bridgeWar);
  }
  
  /**
   * Test the typical deploy of a composite application.
   * Check asynchronous deployment,
   * then undeploy
   * then deploy new version.
   * based on test1 module
   * @throws InterruptedException
   */
  @Test
  public void testDeploy1() throws InterruptedException {
    LOG.log(Level.INFO, "======= test deploy (test1)");
    WAR test1ClientWar = (WAR) factory.createDeployable(container.getId(), test1ClientWarPath, DeployableType.WAR);
    WAR test1CommonWar = (WAR) factory.createDeployable(container.getId(), test1CommonWarPath, DeployableType.WAR);
    WAR test1ServiceWar = (WAR) factory.createDeployable(container.getId(), test1ServiceWarPath, DeployableType.WAR);
    List<Deployable> deployList = new ArrayList<Deployable>();
    deployList.add(test1ClientWar);
    deployList.add(test1CommonWar);
    deployList.add(test1ServiceWar);
    
    deployer.deploy(deployList);
    LOG.info("wait for deploy and synchronization");
    Thread.sleep(tempo*1000);

    String status = getHttpString("http://localhost:8080/bridge/status");
    LOG.log(Level.INFO, "status bridge : {0}", status);
    if (status.equals("init")) {
      Assert.fail("bridge not ready, delay too short ?");
    }
    assertEquals("bridge not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test1-common/status");
    LOG.log(Level.INFO, "status test1-common : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test1-common not ready, delay too short ?");
    }
    assertEquals("test1-common not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test1-service/status");
    LOG.log(Level.INFO, "status test1-service : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test1-service not ready, delay too short ?");
    }
    assertEquals("test1-service not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test1-client/status");
    LOG.log(Level.INFO, "status test1-client : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test1-client not ready, delay too short ?");
    }
    assertEquals("test1-client not ready", "ready", status);
    String result = getHttpString("http://localhost:8080/test1-client/ClientServlet");
    LOG.log(Level.INFO, "result  : {0}", result);
    assertEquals("incorrect result", "value", result);
    
    LOG.log(Level.INFO, "------- deploy all OK");
    
    deployer.undeploy(test1ServiceWar);
    LOG.info("wait for undeploy service");
    Thread.sleep(5000);
    
    result = getHttpString("http://localhost:8080/test1-client/ClientServlet");
    LOG.log(Level.INFO, "result  : {0}", result);
    assertEquals("incorrect result", "service 'service-1.0' not found in registry", result);
    
    LOG.log(Level.INFO, "------- undeploy service OK");

    WAR test1ServiceV2War = (WAR) factory.createDeployable(container.getId(), test1ServiceV2WarPath, DeployableType.WAR);
    deployer.deploy(test1ServiceV2War);
    LOG.info("wait for deploy service v2");
    Thread.sleep(5000);
    
    result = getHttpString("http://localhost:8080/test1-client/ClientServlet");
    LOG.log(Level.INFO, "result  : {0}", result);
    assertEquals("incorrect result", "value v2", result);
    
    LOG.log(Level.INFO, "------- deploy new service OK");
  }
  
  /**
   * Test a composite application with independant dependencies (a jdbc driver not in caller).
   * based on test2 module
   * @throws InterruptedException
   */
  @Test
  public void testDeploy2() throws InterruptedException {
    LOG.log(Level.INFO, "======= test dependencies (test2)");
    WAR test2ClientWar = (WAR) factory.createDeployable(container.getId(), test2ClientWarPath, DeployableType.WAR);
    WAR test2CommonWar = (WAR) factory.createDeployable(container.getId(), test2CommonWarPath, DeployableType.WAR);
    WAR test2ServiceWar = (WAR) factory.createDeployable(container.getId(), test2ServiceWarPath, DeployableType.WAR);
    List<Deployable> deployList = new ArrayList<Deployable>();
    deployList.add(test2ClientWar);
    deployList.add(test2CommonWar);
    deployList.add(test2ServiceWar);
    
    deployer.deploy(deployList);
    LOG.info("wait for deploy and synchronization");
    Thread.sleep(tempo*1000);

    String status = getHttpString("http://localhost:8080/bridge/status");
    LOG.log(Level.INFO, "status bridge : {0}", status);
    if (status.equals("init")) {
      Assert.fail("bridge not ready, delay too short ?");
    }
    assertEquals("bridge not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test2-common/status");
    LOG.log(Level.INFO, "status test2-common : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test2-common not ready, delay too short ?");
    }
    assertEquals("test2-common not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test2-service/status");
    LOG.log(Level.INFO, "status test2-service : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test2-service not ready, delay too short ?");
    }
    assertEquals("test2-service not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test2-client/status");
    LOG.log(Level.INFO, "status test2-client : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test2-client not ready, delay too short ?");
    }
    assertEquals("test2-client not ready", "ready", status);
    String result = getHttpString("http://localhost:8080/test2-client/ClientServlet");
    LOG.log(Level.INFO, "result  : {0}", result);
    assertEquals("incorrect result", "jdbc ok + no classloader leak", result);
    
    LOG.log(Level.INFO, "------- resolution OK");
  } 
  
  /**
   * Test a composite application with incompatible dependencies (a spring 2 application calling a spring 3 application).
   * based on test3 module
   * @throws InterruptedException
   */
  @Test
  public void testDeploy3() throws InterruptedException {
    LOG.log(Level.INFO, "======= test spring (test3)");
    WAR test3ClientWar = (WAR) factory.createDeployable(container.getId(), test3ClientWarPath, DeployableType.WAR);
    WAR test3CommonWar = (WAR) factory.createDeployable(container.getId(), test3CommonWarPath, DeployableType.WAR);
    WAR test3ServiceWar = (WAR) factory.createDeployable(container.getId(), test3ServiceWarPath, DeployableType.WAR);
    List<Deployable> deployList = new ArrayList<Deployable>();
    deployList.add(test3ClientWar);
    deployList.add(test3CommonWar);
    deployList.add(test3ServiceWar);
    
    deployer.deploy(deployList);
    LOG.info("wait for deploy and synchronization");
    Thread.sleep(tempo*1000);

    String status = getHttpString("http://localhost:8080/bridge/status");
    LOG.log(Level.INFO, "status bridge : {0}", status);
    if (status.equals("init")) {
      Assert.fail("bridge not ready, delay too short ?");
    }
    assertEquals("bridge not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test3-common/status");
    LOG.log(Level.INFO, "status test3-common : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test3-common not ready, delay too short ?");
    }
    assertEquals("test3-common not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test3-service/status");
    LOG.log(Level.INFO, "status test3-service : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test3-service not ready, delay too short ?");
    }
    assertEquals("test3-service not ready", "ready", status);

    status = getHttpString("http://localhost:8080/test3-client/status");
    LOG.log(Level.INFO, "status test3-client : {0}", status);
    if (status.equals("init")) {
      Assert.fail("test3-client not ready, delay too short ?");
    }
    assertEquals("test3-client not ready", "ready", status);
    String result = getHttpString("http://localhost:8080/test3-client/ClientServlet");
    LOG.log(Level.INFO, "result  : {0}", result);
    assertEquals("incorrect result", true, result.startsWith("service called"));
    
    LOG.log(Level.INFO, "------- spring 2 calling spring 3 OK");
  } 
  
  /**
   * Stop tomcat after all tests.
   */
  @AfterClass
  public static void after() {
    LOG.info("stopping tomcat");
    container.stop();
  }
  
  /** possible tomcat versions. */
  enum Tomcat {
    TOMCAT5("tomcat5x","http://archive.apache.org/dist/tomcat/tomcat-5/v5.5.35/bin/apache-tomcat-5.5.35.zip"),
    TOMCAT6("tomcat6x","http://archive.apache.org/dist/tomcat/tomcat-6/v6.0.35/bin/apache-tomcat-6.0.35.zip"),
    TOMCAT7("tomcat7x","http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.30/bin/apache-tomcat-7.0.30.zip");
    private final String version;
    private final String downloadURL;
    Tomcat(String version, String downloadURL) {
      this.version = version;
      this.downloadURL = downloadURL;
    }
  }

  /** get string response form http connection. */
  private String getHttpString(String address) {
    String status = "";
    HttpURLConnection conn = null;
    BufferedReader in = null;
    try {
      URL url = new URL(address);
      conn = (HttpURLConnection) url.openConnection();
      int code = conn.getResponseCode();
      LOG.log(Level.INFO, "open {0} code={1}", new Object[]{address, code});
      in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        status = inputLine;
      }
    } 
    catch (MalformedURLException e) {
      status = "bad url";
    }
    catch (IOException e) {
      status = "unreachable servlet";
    }
    finally {
      conn.disconnect();
      if (in != null) {
        try {
          in.close();
        } catch (IOException ignored) {
        }
      }
    }
    return status;
  }
  
}
