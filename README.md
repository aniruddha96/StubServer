# StubServer

This repository contains the stubserver for [VizierDB](https://github.com/VizierDB/vizier-scala).

The server creates stubs([PEP 484](https://peps.python.org/pep-0484/)) for all python libraries installed on the system. Some stubs are fetched from [Typeshed](https://github.com/python/typeshed).

## Prerequisites
Python3, pip3 should be installed and accessible through commandline.
mypy package for python should be installed.
```stubgen``` command should be accessible through commandline directly.

The server creates multiple files in the base directory of the repository, make sure the process has read and write access to the directory.

## Starting the server
To run the server execute from the base directory:
```
./mvnw spring-boot:run
```



