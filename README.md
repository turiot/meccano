## Build modular servlet based applications

The objective of this project is to create a tool allowing **modular** composition of **servlet** applications.

It should be possible to compose a application of multiple applications sharing a common model and acting as publishers or users of services, with the natural Tomcat capability of deploying and reloading parts. A sort of poor man's OSGI for servlets.

Modularity is achieved by offering singleton services, redeployables, versionned along with shared interfaces.

Tomcat (or more precisely the servlet specification) is a good service container, all we need to add is communication between applications. So we could assemble and reassemble applications like **Meccano** parts !

![alt text](http://turiot.github.com/meccano/images/diag.png)

[See project documentation](https://github.com/turiot/meccano/releases/download/1.0/bridge.pdf)
