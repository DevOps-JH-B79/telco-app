pipeline {

    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REPO = '208249468649.dkr.ecr.ap-south-1.amazonaws.com/telco-app'
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
                sh "docker build --no-cache -t $ECR_REPO:$IMAGE_TAG ."
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
                sh "docker push $ECR_REPO:$IMAGE_TAG"
            }
        }

        stage('Update Kubernetes Manifest') {
            steps {
                sh """
                sed -i 's|image:.*|image: $ECR_REPO:$IMAGE_TAG|' k8s/deployment.yaml
                """
            }
        }

        stage('Commit & Push Changes') {
            steps {
                sh """
                git config --global user.email "jenkins@devops.com"
                git config --global user.name "jenkins"

                git checkout develop
                
                git add k8s/deployment.yaml
                git commit -m "Update image to $IMAGE_TAG"
                git push origin develop
                """
            }
        }

    }

    post {
        success {
            echo 'Pipeline executed successfully. ArgoCD will deploy the new version.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}