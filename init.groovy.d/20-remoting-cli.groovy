import jenkins.model.*
import jenkins.*
import hudson.model.*
import hudson.*
import jenkins.model.Jenkins

// DISABLE CLI OVER REMOTING (Best Practice)
jenkins.model.Jenkins.instance.getDescriptor("jenkins.CLI").get().setEnabled(false)