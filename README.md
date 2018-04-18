# Docker Jenkins instances
## Building Jenkins
```
docker build -t jenkinsINdocker .
```
## Running Jenkins
```
docker run -itd \
-p 8080:8080 \
-v /var/log/jenkins:/var/log/jenkins \
--name jenkins \
-h jenkins
jenkins-in-docker
```
## Accessing jenkins

http://localhost:8080

## Credentials
```
u: admin
p: admin 
```
