pipeline {

    agent any

    environment {
        AWS_REGION = "ap-south-1"
        ECR_REPO = "208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app"
        IMAGE_TAG = "latest"
        K8S_NAMESPACE = "telco"
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
                sh "docker build -t telco-app:${IMAGE_TAG} ."
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                aws ecr get-login-password --region $AWS_REGION | \
                docker login --username AWS --password-stdin $ECR_REPO
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                docker tag telco-app:${IMAGE_TAG} $ECR_REPO:${IMAGE_TAG}
                docker push $ECR_REPO:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                kubectl rollout restart deployment telco-app -n $K8S_NAMESPACE
                '''
            }
        }

    }

    post {
        success {
            echo 'Pipeline executed successfully. Deployment completed.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}