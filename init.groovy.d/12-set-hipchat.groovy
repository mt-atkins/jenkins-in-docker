import jenkins.model.*;
import java.lang.reflect.Field;

if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "hipchat" } != null ) {
  println "--> setting hipchat plugin"

  def descriptor = Jenkins.instance.getDescriptorByType(jenkins.plugins.hipchat.HipChatNotifier.DescriptorImpl.class)

  // no setters :-(
  // Groovy can disregard object's pivacy anyway to directly access private
  // fields, but we use a different technique 'reflection' this time
  Field[] fld = descriptor.class.getDeclaredFields();
  for(Field f:fld){
    f.setAccessible(true);
    switch (f.getName()) {
      case "server"         : f.set(descriptor, "hipchat.mydomain.com")
                            break
      case "token"          : f.set(descriptor, "TOKEN")
                            break
      case "buildServerUrl" : f.set(descriptor, "/")
                            break
      case "sendAs"         : f.set(descriptor, "jenkinsbot")
                            break
    }
  }
}