# jenkins

## Setup of Ec2

1. Create ec2 instance on aws with enough storage space
2. Download key (used for ssh into service)
3. Make sure the ec2's inbound rules allow your machine or 0.0.0.0/0 (all)
4. ssh into ec2 instance using key

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
 
