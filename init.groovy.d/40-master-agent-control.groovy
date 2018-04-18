import jenkins.model.*
import jenkins.*
import hudson.model.*
import hudson.*
import jenkins.security.s2m.*

// ENABLE AGENT → MASTER ACCESS CONTROL
Jenkins.instance.injector.getInstance(AdminWhitelistRule.class)
    .setMasterKillSwitch(false);
Jenkins.instance.save()