pipeline {

    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REPO = '://208249468649.dkr.ecr.ap-south-1.amazonaws.com'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Code already checked out by Jenkins'
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Use ${} to ensure variables are interpolated clearly
                sh "docker build --no-cache -t ${ECR_REPO}:${IMAGE_TAG} ."
            }
        }

        stage('Login to ECR') {
            steps {
                sh """
                aws ecr get-login-password --region ${AWS_REGION} | \
                docker login --username AWS --password-stdin ${ECR_REPO}
                """
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh "docker push ${ECR_REPO}:${IMAGE_TAG}"
            }
        }

        stage('Commit & Push Changes') {
            steps {
                script {
                    sh """
                    git config --global user.email "jenkins@devops.com"
                    git config --global user.name "jenkins"

                    # Switch branch first
                    git checkout develop || git checkout -b develop
                    
                    # Update manifest
                    sed -i "s|image:.*|image: ${ECR_REPO}:${IMAGE_TAG}|" k8s/deployment.yaml
                    
                    git add k8s/deployment.yaml
                    git commit -m "Update image to ${IMAGE_TAG}" || echo "No changes to commit"
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
            echo 'Pipeline failed. Check the Docker build logs.'
        }
    }
}
