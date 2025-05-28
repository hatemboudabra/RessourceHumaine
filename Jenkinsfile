pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'portail_rh'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/hatemboudabra/RessourceHumaine.git'
            }
        }

        stage('Build') {
            steps {
                dir('PortailRH') {
                    script {
                        sh "${MAVEN_HOME}/bin/mvn clean install"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                dir('PortailRH') {
                    script {
                        sh "${MAVEN_HOME}/bin/mvn test"
                    }
                }
            }
      
        }

        stage('Upload to Nexus') {
            steps {
                dir('PortailRH') {
                    script {
                        sh "${MAVEN_HOME}/bin/mvn deploy"
                    }
                }
            }
        }

        stage('Run SonarQube Analysis') {
            steps {
                dir('PortailRH') {
                    withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SONAR_TOKEN')]) {
                        script {
                            sh "${MAVEN_HOME}/bin/mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
                        }
                    }
                }
            }
        }

        stage('Publish Code Coverage') {
            steps {
                dir('PortailRH') {
                    script {
                        sh "${MAVEN_HOME}/bin/mvn jacoco:report"
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'PortailRH/target/site/jacoco/*.html', allowEmptyArchive: true
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dir('PortailRH') {
                    script {
                        dependencyCheck additionalArguments: '--scan ./ --noupdate', odcInstallation: 'Dependency-Check'
                        dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'
        }
    }
}