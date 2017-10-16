FROM jenkins
WORKDIR /var/jenkins_home
RUN mkdir -p /var/jenkins_home/init.groovy.d && chown jenkins:jenkins /var/jenkins_home/init.groovy.d
COPY init.groovy.d /var/jenkins_home/init.groovy.d
