pipeline{
    agent any
    environment {
        name = 'megha'
    }
    tools {
        jdk 'Java_Home'
        maven 'Maven_Home'
    }
    parameters{
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Make it true if deploy is needed')
        gitParameter(branch: '', branchFilter: '.*', defaultValue: 'master', description: 'enter the branch name', name: 'branch', quickFilterEnabled: false, selectedValue: 'NONE', sortMode: 'NONE', tagFilter: '*', type: 'PT_BRANCH')
    }
    options{
        timeout(time: 10, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '1'))
    }
    triggers {
        cron('H */4 * * 1-5')
    }
stages{
    stage("checkout"){
        steps{
            echo "iam checkingout"
            checkout([
                $class: 'GitSCM', 
                branches: [[name: "${params.branch}"]], 
                extensions: [], 
                userRemoteConfigs: [[url: 'https://github.com/MeghaHiremat/cobertura-example.git']]
            ])
        }
    }
    stage("testing"){
        steps{
            echo "iam testing"
            sh """
                mvn clean cobertura:cobertura -Dcobertura.report.formats=xml
            """
        }
    }
    stage("coburtura publish"){
        steps{
            echo "cobertura publish"
            cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: '**/target/site/cobertura/coverage.xml', conditionalCoverageTargets: '70, 50, 60', fileCoverageTargets: '70, 60, 50', lineCoverageTargets: '80, 60, 70', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 70, 60', onlyStable: false, packageCoverageTargets: '80, 70, 60', sourceEncoding: 'ASCII', zoomCoverageChart: false
        }
    }
    stage("build"){
        steps{
            echo "building"
            //sh """
               //mvn clean install
            //"""
        }
    }
   stage("deploy"){
         when {
                expression { params.DEPLOY }
            }
        steps{
            echo "deploying"
        }
    }
    stage('Example for environment variables') {
        steps {
            sh 'echo "${name}"'
        }
    }
}
post{
    success{
     echo "build is completed"   
    }
}
}
