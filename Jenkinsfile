pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        // Ensure there are no spaces or hidden characters in this URL
        ECR_REPO = '://208249468649.dkr.ecr.ap-south-1.amazonaws.com'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo "Building version ${env.IMAGE_TAG}"
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Using explicit env. prefix to ensure Jenkins finds the variables
                sh "docker build --no-cache -t ${env.ECR_REPO}:${env.IMAGE_TAG} ."
            }
        }

        stage('Login to ECR') {
            steps {
                sh """
                aws ecr get-login-password --region ${env.AWS_REGION} | \
                docker login --username AWS --password-stdin ${env.ECR_REPO}
                """
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh "docker push ${env.ECR_REPO}:${env.IMAGE_TAG}"
            }
        }

        stage('Commit & Push Changes') {
            steps {
                script {
                    sh """
                    git config --global user.email "jenkins@devops.com"
                    git config --global user.name "jenkins"

                    # Switch branch first while clean
                    git checkout develop || git checkout -b develop
                    
                    # Update manifest - using double quotes for sed to handle variables
                    sed -i "s|image:.*|image: ${env.ECR_REPO}:${env.IMAGE_TAG}|" k8s/deployment.yaml
                    
                    git add k8s/deployment.yaml
                    git commit -m "Update image to ${env.IMAGE_TAG}" || echo "No changes to commit"
                    """

                    withCredentials([usernamePassword(credentialsId: 'jenkins-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh "git push https://\$GIT_USER:\$GIT_PASS@://github.com develop"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the Docker build logs for tag errors.'
        }
    }
}
