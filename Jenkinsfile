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

        stage('Commit & Push Changes') {
            steps {
                script {
                    sh """
                    git config --global user.email "jenkins@devops.com"
                    git config --global user.name "jenkins"

                    # 1. Switch to the target branch first while the workspace is clean
                    git checkout develop || git checkout -b develop
                    
                    # 2. Update the manifest after switching branches
                    sed -i "s|image:.*|image: $ECR_REPO:$IMAGE_TAG|" k8s/deployment.yaml
                    
                    # 3. Commit the change
                    git add k8s/deployment.yaml
                    git commit -m "Update image to $IMAGE_TAG" || echo "No changes to commit"
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
            echo 'Pipeline executed successfully. ArgoCD will deploy the new version.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}
