// HAFTA 2: Testleri her commit'te otomatik çalıştıran pipeline.
// Gerekli Jenkins eklentileri: Pipeline, JUnit, Coverage (JaCoCo raporu için).
// Jenkins'te "Maven 3" ve "JDK 21" isimli tool tanımları olmalı (Manage Jenkins > Tools).

pipeline {
    agent any

    tools {
        maven 'Maven 3'
        jdk 'JDK 21'
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // verify: compile + unit/integration test + JaCoCo raporu
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    recordCoverage(tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/jacoco.xml']])
                }
            }
        }

        // TODO (Hafta 3 - alıştırma): Sadece main branch'te Docker image build eden stage ekle.
        // stage('Docker Build') {
        //     when { branch 'main' }
        //     steps {
        //         sh 'docker build -f product-service/Dockerfile -t product-service:${BUILD_NUMBER} .'
        //     }
        // }
    }

    post {
        success { echo 'Build başarılı' }
        failure { echo 'Build kırıldı - test raporlarına bak' }
    }
}
