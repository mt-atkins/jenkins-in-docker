# Docker Jenkins instances
## Building Jenkins
```
docker build -t jenkins-in-docker .
```
## Running Jenkins
```
docker run -[ it | d ] \
-p 8080:8080 \
-v $pwd:/var/lib/jenkins \
-v /var/run/docker.sock:/var/run/docker.sock \
--name jenkins \
-h jenkins \
jenkins-in-docker
```
## Accessing jenkins

http://localhost:8080

### Credentials
```
u: admin
p: admin
```
