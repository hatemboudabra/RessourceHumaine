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
            environment {
                ODC_DATA_DIR = "${env.WORKSPACE}/dependency-check-data"
                ODC_REPORT_DIR = "${env.WORKSPACE}/dependency-check-reports"
            }

            steps {
                dir('PortailRH') {
                    script {
                        try {
                            // Créer les répertoires nécessaires
                            sh "mkdir -p ${ODC_DATA_DIR} ${ODC_REPORT_DIR}"

                            // Exécuter Dependency-Check avec des paramètres optimisés
                            dependencyCheck additionalArguments: """
                                --scan ./
                                --format ALL
                                --out ${ODC_REPORT_DIR}
                                --data ${ODC_DATA_DIR}
                                --disableAssembly
                                --disableNodeJS
                                --disableYarnAudit
                                --failOnCVSS 7
                                --log ${ODC_REPORT_DIR}/dependency-check.log
                                --project ${env.JOB_NAME}
                                --suppression ${env.WORKSPACE}/PortailRH/dependency-check-suppressions.xml
                            """, odcInstallation: 'Dependency-Check'

                            // Publier les rapports
                            dependencyCheckPublisher pattern: "${ODC_REPORT_DIR}/dependency-check-report.*"

                        } catch (Exception e) {
                            // En cas d'échec, essayer avec --noupdate
                            echo "Échec de l'analyse initiale, tentative avec --noupdate..."
                            dependencyCheck additionalArguments: """
                                --scan ./
                                --noupdate
                                --format ALL
                                --out ${ODC_REPORT_DIR}
                            """, odcInstallation: 'Dependency-Check'

                            dependencyCheckPublisher pattern: "${ODC_REPORT_DIR}/dependency-check-report.*"
                        }
                    }
                }
            }

            post {
                always {
                    // Archiver les résultats et logs
                    archiveArtifacts artifacts: "${ODC_REPORT_DIR}/**", allowEmptyArchive: true

                    // Enregistrer les résultats au format JUnit
                    junit "${ODC_REPORT_DIR}/dependency-check-report.xml"

                    // Afficher un résumé
                    script {
                        if (fileExists("${ODC_REPORT_DIR}/dependency-check.log")) {
                            echo "=== Résumé de l'analyse Dependency-Check ==="
                            sh """
                                grep -i 'High' ${ODC_REPORT_DIR}/dependency-check-report.xml | wc -l | xargs echo 'Vulnérabilités High:'
                                grep -i 'Critical' ${ODC_REPORT_DIR}/dependency-check-report.xml | wc -l | xargs echo 'Vulnérabilités Critical:'
                                tail -n 20 ${ODC_REPORT_DIR}/dependency-check.log
                            """
                        }
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
