pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REPO = '208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${env.ECR_REPO}:${env.IMAGE_TAG} ."
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

        stage('Deploy to EKS') {
            steps {
                sh """
                kubectl set image deployment/telco-app \
                telco-app=${env.ECR_REPO}:${env.IMAGE_TAG} \
                -n telco
                """
            }
        }
    }
}