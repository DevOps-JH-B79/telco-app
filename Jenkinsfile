/* groovylint-disable-next-line CompileStatic */
pipeline {

    agent any

    environment {
        IMAGE_TAG = "latest"
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
                /* groovylint-disable-next-line GStringExpressionWithinString */
                sh 'docker build -t telco-app:${IMAGE_TAG} .'
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                aws ecr get-login-password --region ap-south-1 | \
                docker login --username AWS --password-stdin 208249468649.dkr.ecr.ap-south-1.amazonaws.com
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                /* groovylint-disable-next-line GStringExpressionWithinString */
                sh '''
                docker tag telco-app:${IMAGE_TAG} 208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app:${IMAGE_TAG}
                docker push 208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh 'kubectl rollout restart deployment telco-app -n telco'
            }
        }

    }
}
