# jenkins

## Setup of Ec2

1. Create ec2 instance on aws with enough storage space
2. Download key (used for ssh into service)
3. Make sure the ec2's inbound rules allow your machine or 0.0.0.0/0 (all)
4. ssh into ec2 instance using key
5. setup tools (git, nvm, node, sonar)
  ```sh
  sudo yum remove -y java
  sudo yum install -y java-1.8.0-openjdk
  sudo yum install -y git
  sudo yum update -y
  sudo -i
  curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.32.1/install.sh | bash
  source ~/.bashrc
  nvm install stable
  nvm alias default stable
  ```

## Install jenkins

1. Run the below commands in terminal:

  ```sh
  sudo wget -O /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat/jenkins.repo
  sudo rpm --import https://jenkins-ci.org/redhat/jenkins-ci.org.key
  sudo yum install jenkins
  sudo service jenkins start
  sudo chkconfig jenkins on
  ```

2. In your browser, navigate to the ec2 address with port 8080
3. It will want a generated password from the log file (may require sudo).
  
  ```sh
  more var/log/jenkins/jenkins.log
  ```
   
   Keep scrolling until you see something like:
   
  ```sh
  *************************************************************
  *************************************************************
  *************************************************************

  Jenkins initial setup is required. An admin user has been created and a password generated.
  Please use the following password to proceed to installation:

  <your password>

  This may also be found at: /var/lib/jenkins/secrets/initialAdminPassword

  *************************************************************
  *************************************************************
  *************************************************************
  ```
    
4. Log in
5. Install standard plugins
6. Change Admin password and username
7. Install the following plugings:
  * Dynamic Parameter Plug-in
  * Environment File Plugin
 

## Setup sonar
1. Run the following commands on the ec2 terminal:
  ```sh
  https://sonarsource.bintray.com/Distribution/sonarqube/sonarqube-5.6.4.zip
  unzip sonarqube-5.6.4.zip
  rm -rf sonarqube-5.6.4.zip
  sudo mv sonarqube-5.6.4 /usr/local/
  sudo ln -s /usr/local/sonarqube-5.6.4/ /usr/local/sonar
  ```
2. Create mysql instance in aws. Make sure it is on the same vpc as ec2.
3. Create a blank schema and user with permission to that schema
4. On ec2 instance run the following command in terminal:
  ```sh
  sudo vi /usr/local/sonar/conf/sonar.properties
  ```
5. Uncomment and set sonar.jdbc.username, sonar.jdbc.password, and sonar.jdbc.url
6. Save file
7. Run the following to start sonar
  ```sh
  sudo sh /usr/local/sonar/bin/linux-x86-64/sonar.sh start
  sudo sh /usr/local/sonar/bin/linux-x86-64/sonar.sh stop
  ```
  Should say running.
8. Add SONAR_HOME
  ```sh
  sudo vi ~/.bash_profile
  ```
  Add the following, also add $SONAR_HOME to the path:
  ```
  SONAR_HOME=/usr/local/sonar
  export SONAR_HOME
  ```
9. To setup as a service:
  ```sh
  sudo touch /etc/init.d/sonar
  sudo vi /etc/init.d/sonar
  ```
  Add the following text to that sonar file:
  ```
  #!/bin/sh
  #
  # rc file for SonarQube
  #
  # chkconfig: 345 96 10
  # description: SonarQube system (www.sonarsource.org)
  #
  ### BEGIN INIT INFO
  # Provides: sonar
  # Required-Start: $network
  # Required-Stop: $network
  # Default-Start: 3 4 5
  # Default-Stop: 0 1 2 6
  # Short-Description: SonarQube system (www.sonarsource.org)
  # Description: SonarQube system (www.sonarsource.org)
  ### END INIT INFO
  
  /usr/bin/sonar $*
  ```
  Then run:
  ```sh
  sudo chmod 755 /etc/init.d/sonar
  sudo chkconfig --add sonar
  ```

## Setup Sonar-runner
1. Run the following on the ec2 instance:
  ```sh
  wget http://repo1.maven.org/maven2/org/codehaus/sonar/runner/sonar-runner-dist/2.4/sonar-runner-dist-2.4.zip
  unzip sonar-runner-dist-2.4.zip
  rm -rf sonar-runner-dist-2.4.zip
  sudo mv sonar-runner-2.4 /usr/local/
  sudo ln -s /usr/local/sonar-runner-2.4/ /usr/local/sonar-runner
  ```