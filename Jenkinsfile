pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REPO = '208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app'
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
                sh "docker build --no-cache -t ${env.ECR_REPO}:${env.IMAGE_TAG} ."
            }
        }

        stage('Login to ECR') {
            steps {
                sh """
                aws ecr get-login-password --region ${env.AWS_REGION} | \
                docker login --username AWS --password-stdin 208249468649.dkr.ecr.ap-south-1.amazonaws.com
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

                    git checkout develop || git checkout -b develop

                    sed -i "s|image:.*|image: ${env.ECR_REPO}:${env.IMAGE_TAG}|" k8s/deployment.yaml

                    git add k8s/deployment.yaml
                    git commit -m "Update image to ${env.IMAGE_TAG}" || echo "No changes to commit"
                    """

                    withCredentials([usernamePassword(credentialsId: 'jenkins-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh """
                        git push https://$GIT_USER:$GIT_PASS@github.com/DevOps-JH-B79/telco-app.git develop
                        """
                    }

                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully. ArgoCD will deploy the new version.'
        }
        failure {
            echo 'Pipeline failed. Check logs.'
        }
    }
}