import hudson.model.*;
import jenkins.model.*;
import hudson.plugins.ec2.*;
import com.amazonaws.services.ec2.model.*;

if ( Jenkins.instance.pluginManager.activePlugins.find { it.shortName == "ec2" } != null ) {
  println "--> setting ec2 plugin"

  ///////////////// GLOBAL SETTINGS ///////////////////////////////////////////
  // should use the same tag for all slave templates
  def ec2Tags = [
    new EC2Tag('Name', 'jenkins-builder.elastic.us-west-2a'),
    new EC2Tag('created_by', '<%= node['fqdn'] %>'), // master node
    new EC2Tag('Service', 'jenkins'),
    new EC2Tag('Team', 'releng'),
    new EC2Tag('Stage', 'prod')
  ] as List
  UnixData unixData = new UnixData(null, '22') // linux box

  ////////////////////// SLAVE INSTANCE TEMPLATES /////////////////////////////
  SlaveTemplate awsTemplate = new SlaveTemplate(
    'ami-37e7af07',                                     // ami
    'us-west-2a',                                       // zone
    null,                                               // spotconfiguration
    'corp, jenkins',                                    // security groups
    '/home/jenkins/slave-root',                         // remote fs
    InstanceType.M3Large,                               // instance type
    'aws',                                              // jenkins label
    hudson.model.Node.Mode.NORMAL,                      // hudson.model.Node.Mode
    'aws builder us-west-2a',                           // description
    """#!/bin/bash

source /usr/local/lib/bob/rvm_s3.sh || true
downloadRvmRubiesS3 || true""",                     // init script
    '',                                             // userdata
    '1',                                            // num executors
    'jenkins',                                      // remote admin user
    unixData,                // unix or windows (hudson.plugins.ec2.AMITypeData)
    '',                                             // slave jvmopts
    true,                                           // stop on terminate?
    'subnet-cxxxxxxx',                              // subnet id
    ec2Tags,                                        // ec2 tags
    '-5',                                           // idle termination minutes
    false,                                          // use private dns name?
    '200',                                          // instance cap per ami
    '',                                             // IAM instance profile
    false,                                          // use ephemeral devices?
    false,                                          // use dedicated tenancy?
    '1200',                                         // launch timeout
    false,                                          // associate public ip?
    ''                                              // custom device mapping?
  )
  // a list of slave templates
  def slaveTemplates = [awsTemplate]

  ////////////////////////////// EC2 CLOUDs ///////////////////////////////////
  def ec2Cloud = new AmazonEC2Cloud(
    'SAMPLEID',                             // access id
    '<%= @creds['plugins']['ec2']['secret_key'] %>',    // secret key
    'us-west-1',                                        // region
    """<%= @creds['plugins']['ec2']['private_key'] %>""",   // private key
    '500',                                           // instance cap
    slaveTemplates                                   // list of slave templates
  )

  //////////////////////////// ADDING EC2 CLOUDS //////////////////////////////
  def cloudList = Jenkins.instance.clouds

  // avoid duplicate cloud provider on the cloud list
  if ( cloudList.getByName(ec2Cloud.name) ) {
    cloudList.remove(cloudList.getByName(ec2Cloud.name))
  }
  cloudList.add(ec2Cloud)
}