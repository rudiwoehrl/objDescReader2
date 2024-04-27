# objDescReader
Read and process object descriptions from DirX Identity domain.

Project to explore how to replace longterm the object description implementation included in the Storage layer.
Minimizes the dependencies, namely get rid of Storage and Storage Url. Relies on Jackson XML and Netscape LDAP for parsing object description LDAP entries and transform them to data object without business logic.
