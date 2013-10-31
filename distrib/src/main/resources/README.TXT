
Bonita Open Solution deploy version XXX_VERSION_XXX

Contents of this package:

|-- bonita_execution_engine                       Engine dependencies and applications
|   |-- bonita_client
|   |   `-- libs                                  Client required libraries
|   |      `-- *.jar
|   |-- engine
|   |   `-- libs                                  Engine libraries
|   |      `-- *.jar
|   |-- interfaces                                Web applications and applications that expose the engine
|   |   |-- EJB                                   Applications which expose the engine using EJB
|   |   |   |-- EJB2                              EJB2 version
|   |   |   |   `-- bonita.ear
|   |   |   `-- EJB3                              EJB3 version
|   |   |       `-- bonita.ear
|   |   `-- REST                                  REST web application
|   |       |-- with_engine                       
|   |       |   `-- bonita-server-rest.war        REST web application with engine libraries
|   |       `-- without_engine
|   |           `-- bonita-server-rest-light.war  REST web application without engine libraries
|   |     
|   `-- migration                                 Migration and re-indexation scripts
|-- bonita_user_experience
|   |-- with_execution_engine_without_client      Bonita User Experience with the engine embedded
|   |   `-- bonita.war
|   |-- without_execution_engine_with_client      Bonita User Experience without the engine but with bonita client
|   |   `-- bonita.war
|   `-- without_execution_engine_without_client   Bonita User Experience without engine and without bonita client -- must have either bonita-server + deps or bonita-client + deps in the lib folder of the application server
|       `-- bonita.war
|-- conf                                       
|   |-- bonita                                    BONITA_HOME folder, contains all Bonita Open Solution configuration files
|   `-- external                                  External configuration not directly related to Bonita Open Solution
|       |-- logging
|       |   `-- logging.properties                Default logging configuration
|       |-- security
|       |   |-- jaas-standard.cfg                 Standard JAAS configuration file
|       |   |-- jaas-*.cfg                        Configuration examples for other application servers
|       `-- xcmis
|           `-- ext-exo-conf                      xCMIS configuration files
|-- license                                       License files
|-- xcmis                                         xCMIS server which stores attachments and documents
`-- README.txt                                    You are here!

You can find guides to configure JBoss or Tomcat using this package on http://www.bonitasoft.org/wiki/doku.php
