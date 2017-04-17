import jenkins.model.*;
import net.sf.json.*;
import com.sonyericsson.hudson.plugins.gerrit.trigger.*;

if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "gerrit-trigger" } != null ) {
  println "--> setting gerrit-trigger plugin"

  def gerritPlugin = Jenkins.instance.getPlugin(com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.class)
  gerritPlugin.getPluginConfig().setNumberOfReceivingWorkerThreads(3)
  gerritPlugin.getPluginConfig().setNumberOfSendingWorkerThreads(1)

  def serverName = "lookout-gerrit"
  GerritServer server = new GerritServer(serverName)
  def config = server.getConfig()

  def triggerConfig = [
    'gerritHostName':"gerrit.mydomain.com",
    'gerritSshPort':29418,
    'gerritUserName':"jenkins",
    'gerritFrontEndUrl':"https://gerrit.mydomain.com",
    'gerritBuildCurrentPatchesOnly':true,
    'gerritBuildStartedVerifiedValue':0,
    'gerritBuildStartedCodeReviewValue':0,
    'gerritBuildSuccessfulVerifiedValue':1,
    'gerritBuildSuccessfulCodeReviewValue':0,
    'gerritBuildFailedVerifiedValue':-1,
    'gerritBuildFailedCodeReviewValue':0,
    'gerritBuildUnstableVerifiedValue':-1,
    'gerritBuildUnstableCodeReviewValue':0,
    'gerritBuildNotBuiltVerifiedValue':0,
    'gerritBuildNotBuiltCodeReviewValue':0,
    'enableManualTrigger':true,
    'enablePluginMessages':true,
    'buildScheduleDelay':3,
    'dynamicConfigRefreshInterval':30,
    'watchdogTimeoutMinutes':0,
    'verdictCategories': [
      [ 'verdictValue':'CRVW', 'verdictDescription':'Code Review'],
      [ 'verdictValue':'VRIF', 'verdictDescription':'Verified']
    ] as LinkedList
  ]

  config.setValues(JSONObject.fromObject(triggerConfig))
  server.setConfig(config)

  // avoid duplicate servers on the server list
  if ( gerritPlugin.containsServer(serverName) ) {
    gerritPlugin.removeServer(gerritPlugin.getServer(serverName))
  }
  gerritPlugin.addServer(server)
}