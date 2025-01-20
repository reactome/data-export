// This Jenkinsfile is used by Jenkins to run the 'DataExport' step of Reactome's release.
// It requires that the 'DiagramConverter' step has been run successfully before it can be run.

import org.reactome.release.jenkins.utilities.Utilities

// Shared library maintained at 'release-jenkins-utils' repository.
def utils = new Utilities()

pipeline{
	agent any
	// Set output folder that will contain the files generated by this step.
    	environment {
        	OUTPUT_FOLDER = "export"
		ECR_URL = 'public.ecr.aws/reactome/data-export'
		CONT_NAME = 'data_export_container'
		CONT_ROOT = '/opt/data-export'
    	}
    
	stages{
		// This stage checks that upstream project 'DiagramConverter' was run successfully.
		stage('Check DiagramConverter build succeeded'){
			steps{
				script{
				    utils.checkUpstreamBuildsSucceeded("File-Generation/job/DiagramConverter/")
				}
			}
		}

		
		stage('Setup: Pull and clean docker environment'){
			steps{
				sh "docker pull ${ECR_URL}:latest"
				sh """
					if docker ps -a --format '{{.Names}}' | grep -Eq '${CONT_NAME}'; then
						docker rm -f ${CONT_NAME}
					fi
				"""
			}
		}
		
		// Execute the jar file, producing data-export files.
		stage('Main: Run Data-Export'){
			steps{
				script{
					def releaseVersion = utils.getReleaseVersion()
					sh "mkdir -p ${env.OUTPUT_FOLDER}"
					sh "rm -rf ${env.OUTPUT_FOLDER}/*"
					withCredentials([usernamePassword(credentialsId: 'neo4jUsernamePassword', passwordVariable: 'pass', usernameVariable: 'user')]){
						// This is a very memory-intensive step, and as such it is necessary to stop unused services to get it to run to completion.
						// At time of writing (December 2020), the source of the problem seems to come from the 'Ensembl' mappings.
						sh "sudo service mysql stop"
						sh "sudo service tomcat9 stop"

						sh """\
							    docker run -v \$(pwd)/${env.OUTPUT_FOLDER}:${CONT_ROOT}/${env.OUTPUT_FOLDER} --net=host --name ${CONT_NAME} ${ECR_URL}:latest /bin/bash -c 'java -Xmx${env.JAVA_MEM_MAX}m -jar target/data-export-exec.jar --user $user --password $pass --output ./${env.OUTPUT_FOLDER} --verbose'
	                                        """
                                                sh "sudo chown jenkins:jenkins ${env.OUTPUT_FOLDER}"
						sh "sudo service mysql start"
						sh "sudo service tomcat9 start"
						// Archive the files produced by the step for S3.
						sh "tar -zcvf export-v${releaseVersion}.tgz ${env.OUTPUT_FOLDER}"
					}
				}
			}
		}
		// This stage outputs the difference in line counts for data-export files between releases.
		stage('Post: Compare Data-Export file line counts between releases') {
		    steps{
		        script{
			def releaseVersion = utils.getReleaseVersion()
			def previousReleaseVersion = utils.getPreviousReleaseVersion()
			def previousExportsArchive = "export-v${previousReleaseVersion}.tgz"
			def currentDir = pwd()

			sh "mkdir -p ${previousReleaseVersion}"

			// Download data-export files archive from previous release from S3. 
			sh "aws s3 --no-progress cp s3://reactome/private/releases/${previousReleaseVersion}/data_export/data/${previousExportsArchive} ${previousReleaseVersion}/"
			dir("${previousReleaseVersion}"){
				sh "tar -xf ${previousExportsArchive}"
			}
			// Output line counts between files.
			utils.outputLineCountsOfFilesBetweenFolders("${env.OUTPUT_FOLDER}", "${previousReleaseVersion}/${env.OUTPUT_FOLDER}", "$currentDir")
			sh "rm -r ${previousReleaseVersion}*"
		        }
		    }
		}
		// Move all data-export files to the downloads folder. At time of writing, these files aren't gzipped.
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
