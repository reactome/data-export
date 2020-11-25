// This Jenkinsfile is used by Jenkins to run the DataExport step of Reactome's release.
// It requires that the DiagramConverter step has been run successfully before it can be run.

import org.reactome.release.jenkins.utilities.Utilities

// Shared library maintained at 'release-jenkins-utils' repository.
def utils = new Utilities()

pipeline{
	agent any
	
    environment {
        OUTPUT_FOLDER = "export"
    }
    
	stages{
		// This stage checks that upstream project DiagramConverter was run successfully.
		stage('Check DiagramConverter build succeeded'){
			steps{
				script{
				    utils.checkUpstreamBuildsSucceeded("File-Generation/job/DiagramConverter/")
				}
			}
		}
		// This stage builds the jar file using maven.
		stage('Setup: Build jar file'){
			steps{
				script{
					sh "mvn clean package"
				}
			}
		}
		// Execute the jar file, producing data-export files.
		stage('Main: Run Data-Export'){
			steps{
				script{
					def releaseVersion = utils.getReleaseVersion()
					sh "mkdir -p ${env.OUTPUT_FOLDER}"
					withCredentials([usernamePassword(credentialsId: 'neo4jUsernamePassword', passwordVariable: 'pass', usernameVariable: 'user')]){
						sh "sudo service mysql stop"
						sh "sudo service tomcat7 stop"

						sh "java -Xmx${env.JAVA_MEM_MAX}m -jar target/data-export-jar-with-dependencies.jar --user $user --password $pass --output ./${env.OUTPUT_FOLDER} --verbose"

						sh "sudo service mysql start"
						sh "sudo service tomcat7 start"
						sh "tar -zcvf export-v${releaseVersion}.tgz ${env.OUTPUT_FOLDER}"
					}
				}
			}
		}
		stage('Post: Compare Data-Export file line counts between releases') {
		    steps{
		        script{
		            def releaseVersion = utils.getReleaseVersion()
		            def previousReleaseVersion = utils.getPreviousReleaseVersion()
		            def previousExportsArchive = "export-v${previousReleaseVersion}.tgz"
		            def currentDir = pwd()
		            
		            sh "mkdir -p ${previousReleaseVersion}"
		            sh "aws s3 --no-progress cp s3://reactome/private/releases/${previousReleaseVersion}/data_export/${previousExportsArchive} ${previousReleaseVersion}/"
		            dir("${previousReleaseVersion}"){
		                sh "tar -xf ${previousExportsArchive}"
		            }
		            
			    utils.outputLineCountsOfFilesBetweenFolders("${env.OUTPUT_FOLDER}", "${previousReleaseVersion}/${env.OUTPUT_FOLDER}", "$currentDir")
			    sh "rm -r ${previousReleaseVersion}*"
		        }
		    }
		}
		stage('Post: Move export files to download folder') {
		    steps{
		        script{
		            def releaseVersion = utils.getReleaseVersion()
		            def downloadPath = "${env.ABS_DOWNLOAD_PATH}/${releaseVersion}"
		            sh "mv ${env.OUTPUT_FOLDER}/* ${downloadPath}/ "
		        }
		    }
		}
		// Archive everything on S3, and move the 'diagram' folder to the download/vXX folder.
		stage('Post: Archive Outputs'){
			steps{
				script{
					def releaseVersion = utils.getReleaseVersion()
				    	def dataFiles = ["export-v${releaseVersion}.tgz"]
					def logFiles = []
					def foldersToDelete = []
					utils.cleanUpAndArchiveBuildFiles("data_export", dataFiles, logFiles, foldersToDelete)
				}
			}
		}
	}
}
