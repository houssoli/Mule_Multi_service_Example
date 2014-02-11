This example illustrates multi services mule application. 
The different mule services constructs a simple workflow, where each service passes its outbound data 
into the next service in line via the Space.

GENERAL DESCRIPTION:
--------------------
  The project consists of 4 modules: common, feeder , Verifier and Approver. The common
module includes all the shared resources and classes between all other modules. 
In our case, the common module includes the "Data" class which
is written and taken from the Space.

- A Data-Grid with 2 partitions and one backup for each partition.
- A Feeder pushing Data objects with state=0 into the space.
- The Verifier Service consuming state=0 objects and moving these into state=1
- The Approver Service consuming state=1 objects and moving these into state=2

JVM: >= 5.

BUILDING, PACKAGING, RUNNING, DEPLOYING
---------------------------------------

*Note:

  In order to use Mule with GigaSpaces, mule jar files must be copied to the GigaSpaces
installation under GSHOME/lib/mule (if the mule directory does not exists, create it).
In order to obtain the mule jar files, please download mule 2.1.2 from http://mule.mulesource.org.

The following needs to be copied:
  - From MULEHOME/lib/mule and into GSHOME/lib/mule: mule-core, mule-module-client,
mule-module-spring-config, mule-module-spring-extras, mule-transport-quartz, mule-transport-stdio,
mule-transport-vm, mule-transport-http.
  - From MULEHOME/lib/opt and into GSHOME/lib/mule: commons-beanutils, commons-collections,
commons-io, commons-lang, commons-pool, jug.osgi-2.0.0, quartz-all.

The above creates the ability to deploy a mule processing unit that does not have the mule jars files
in it. It is also possible to package the mule jar files into the processing unit "lib" directory, without
the need to create the GSHOME/lib/mule directory at all.

Quick list:

* mvn compile: Compiles the project.
* mvn os:run: Runs the project.
* mvn test: Runs the tests in the project.
* mvn package: Compiles and packages the project.
* mvn os:run-standalone: Runs a packaged application (from the jars).
* mvn os:deploy: Deploys the project onto the Service Grid.
* mvn os:undeploy: Removes the project from the Service Grid.

  In order to build the example, a simple "mvn compile" executed from the root of the 
project will compile all the different modules.

  Packaging the application can be done using "mvn package" (note, by default, it also
runs the tests, in order to disable it, use -DskipTests). The packaging process jars up 
the common module. The feeder and processor modules packaging process creates a 
"processing unit structure" directory within the target directory called [app-name]-[module].
It also creates a jar from the mentioned directory called [app-name]-[module].jar.

  In order to simply run both the processor and the feeder (after compiling), "mvn os:run" can be used.
This will run a single instance of the processor and a single instance of the feeder within
the same JVM using the compilation level classpath (no need for packaging). 
  A specific module can also be executed by itself, which in this case, executing more than 
one instance of the processing unit can be done. For example, running the processor module with 
a cluster topology of 2 partitions, each with one backup, the following command can be used:
mvn os:run -Dmodule=feeder.
mvn os:run -Dmodule=approver.
mvn os:run -Dmodule=verifier.

  In order to run a packaged processing unit, "mvn package os:run-standalone" can be used (if
"mvn package" was already executed, it can be omitted). This operation will run the processing units
using the packaged jar files. Running a specific module with a cluster topology can be executed using:
mvn package os:run-standalone -Dmodule=processor -Dcluster="total_members=2,1".

  Deploying the application requires starting up a GSM and at least 2 GSCs (scripts located under
the bin directory within the GigaSpaces installation). Once started, running "mvn package os:deploy"
will deploy the two processing units. 
  When deploying, the SLA elements within each processing unit descriptor (pu.xml) are taken into 
account. This means that by default when deploying the application, 2 partitions, each with 
one backup will be created for the processor, and a single instance of the feeder will be created.
  A special note regarding groups and deployment: If the GSM and GSCs were started under a specific 
group, the -Dgroups=[group-name] will need to be used in the deploy command.

WORKING WITH ECLIPSE
--------------------

  In order to generate eclipse project the following command need to be executed from the root of
the application: "mvn eclipse:eclipse". Pointing the Eclipse import existing project wizard
to the application root directory will result in importing the three modules.
If this is a fresh Eclipse installation, the M2_REPO needs be defined and pointed to the local 
maven repository (which resides under USER_HOME/.m2/repository).

  The application itself comes with built in launch targets allowing to run the processor and the 
feeder using Eclipse run (or debug) targets.

For more information about the Mule ESB please refer to:
http://www.gigaspaces.com/wiki/display/XAP8/Mule+ESB
