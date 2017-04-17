import jenkins.model.*;
import hudson.security.*;

// JVM did not like 'hypen' in the class name, it will crap out saying it is
// illegal class name.
class BuildPermission {
  static buildNewAccessList(userOrGroup, permissions) {
    def newPermissionsMap = [:]
    permissions.each {
      newPermissionsMap.put(Permission.fromId(it), userOrGroup)
    }
    return newPermissionsMap
  }
}

if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "matrix-auth" } != null ) {
  if ( Jenkins.instance.isUseSecurity() ) {
    println "--> setting project matrix authorization strategy"
    strategy = new hudson.security.ProjectMatrixAuthorizationStrategy()

    //---------------------------- anonymous ----------------------------------
    // NOTE: It is very bad to let anonymous to install/upload plugins, but
    // that's how our chef run as to install plugins. :-/
    anonymousPermissions = [
      "hudson.model.Hudson.Read",
      "hudson.model.Item.Read",
    ]
    anonymous = BuildPermission.buildNewAccessList("anonymous", anonymousPermissions)
    anonymous.each { p, u -> strategy.add(p, u) }

    //------------------- fa-rel-jenkins --------------------------------------
    faUserPermissions = [
      "hudson.model.Hudson.Administer",
      "hudson.model.Hudson.ConfigureUpdateCenter",
      "hudson.model.Hudson.Read",
      "hudson.model.Hudson.RunScripts",
      "hudson.model.Hudson.UploadPlugins",
      "hudson.model.Item.Read"
    ]
    faUser = BuildPermission.buildNewAccessList("<%= @creds['plugins']['active-directory']['user'] %>", faUserPermissions)
    faUser.each { p, u -> strategy.add(p, u) }

    //------------------- authenticated ---------------------------------------
    authenticatedPermissions = [
      "hudson.model.Hudson.Read",
      "hudson.model.Item.Build",
      "hudson.model.Item.Configure",
      "hudson.model.Item.Create",
      "hudson.model.Item.Delete",
      "hudson.model.Item.Discover",
      "hudson.model.Item.Read",
      "hudson.model.Item.Workspace",
      "hudson.model.Run.Delete",
      "hudson.model.Run.Update",
      "hudson.model.View.Configure",
      "hudson.model.View.Create",
      "hudson.model.View.Delete",
      "hudson.model.View.Read",
      "hudson.model.Item.Cancel"
    ]
    // plugin 'gerrit-trigger' permissions
    if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "gerrit-trigger" } != null ){
      authenticatedPermissions.addAll(["com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.ManualTrigger"])
    }

    // plugin 'promoted-builds' permissions
    if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "promoted-builds" } != null ){
      authenticatedPermissions.addAll(["hudson.plugins.promoted_builds.Promotion.Promote"])
    }

    authenticated = BuildPermission.buildNewAccessList("authenticated", authenticatedPermissions)
    authenticated.each { p, u -> strategy.add(p, u) }

    //----------------- jenkins admin -----------------------------------------
    jenkinsAdminPermissions = [
      "hudson.model.Hudson.Administer",
      "hudson.model.Hudson.ConfigureUpdateCenter",
      "hudson.model.Hudson.Read",
      "hudson.model.Hudson.RunScripts",
      "hudson.model.Hudson.UploadPlugins",
      "hudson.model.Computer.Build",
      "hudson.model.Computer.Build",
      "hudson.model.Computer.Configure",
      "hudson.model.Computer.Connect",
      "hudson.model.Computer.Create",
      "hudson.model.Computer.Delete",
      "hudson.model.Computer.Disconnect",
      "hudson.model.Run.Delete",
      "hudson.model.Run.Update",
      "hudson.model.View.Configure",
      "hudson.model.View.Create",
      "hudson.model.View.Read",
      "hudson.model.View.Delete",
      "hudson.model.Item.Create",
      "hudson.model.Item.Delete",
      "hudson.model.Item.Configure",
      "hudson.model.Item.Read",
      "hudson.model.Item.Discover",
      "hudson.model.Item.Build",
      "hudson.model.Item.Workspace",
      "hudson.model.Item.Cancel"
     ]

    // plugin 'credentials' permissions
    if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "credentials" } != null ){
      jenkinsAdminPermissions.addAll(["com.cloudbees.plugins.credentials.CredentialsProvider.Create",
                                      "com.cloudbees.plugins.credentials.CredentialsProvider.Delete",
                                      "com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains",
                                      "com.cloudbees.plugins.credentials.CredentialsProvider.Update",
                                      "com.cloudbees.plugins.credentials.CredentialsProvider.View"])
    }

    // plugin 'gerrit-trigger' permissions
    if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "gerrit-trigger" } != null ){
      jenkinsAdminPermissions.addAll(["com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.ManualTrigger",
                                      "com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.Retrigger"])
    }
    // plugin 'promoted-builds' permissions
    if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "promoted-builds" } != null ){
      jenkinsAdminPermissions.addAll(["hudson.plugins.promoted_builds.Promotion.Promote"])
    }

    jenkinsAdmin = BuildPermission.buildNewAccessList("GRP-JenkinsAdmins", jenkinsAdminPermissions)
    jenkinsAdmin.each { p, u -> strategy.add(p, u) }

    //-------------------------------------------------------------------------

    // now set the strategy globally
    Jenkins.instance.setAuthorizationStrategy(strategy)
  }
}
