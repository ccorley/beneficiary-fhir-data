#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

/**
 * <p>
 * This script will be run by Jenkins when building this repository. Specifically, it
 * contains methods that will handle deploying the applications to the environment, such
 * as {@link #deploy(String,String,BuildResult)}.
 * </p>
 */


/**
 * Models the results of a call to {@link #buildAppAmis}: contains the IDs of the AMIs that were built.
 */
class AmiIds implements Serializable {
	/**
	 * <p>
	 * The ID of the base "platinum" AMI to use for new instances/AMIs, or <code>null</code> if such an AMI does not yet exist.
	 * </p>
	 * <p>
	 * Why "platinum"? Because it's essentially a respin of the CCS environment's "gold master", with
	 * updates and additional tooling and configuration applied.
	 * </p>
	 */
	String platinumAmiId

	/**
	 * The ID of the AMI that will run the BFD Pipeline service, or <code>null</code> if such an AMI does not yet exist.
	 */
	String bfdPipelineAmiId

	/**
	 * The ID of the AMI that will run the BFD Server service, or <code>null</code> if such an AMI does not yet exist.
	 */
	String bfdServerAmiId
}


/**
 * Finds the IDs of the latest BFD AMIs (if any) in the environment.
 *
 * @return a new {@link AmiIds} instance detailing the already-existing, latest AMIs (if any) that are now available for use
 * @throws RuntimeException An exception will be bubbled up if the AMI-builder tooling returns a non-zero exit code.
*/
def findAmis() {
	// Replace this lookup either with a lookup in SSM or in a build artifact.
	return new AmiIds(
		platinumAmiId: sh(
			returnStdout: true,
			script: "/usr/local/bin/aws ec2 describe-images --owners self --filters \
			'Name=name,Values=bfd-platinum-??????????????' \
			'Name=state,Values=available' --region us-east-1 --output json | \
			jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId'"
    ).trim(),
    bfdPipelineAmiId: sh(
      returnStdout: true,
      script: "/usr/local/bin/aws ec2 describe-images --owners self --filters \
			'Name=name,Values=bfd-etl-??????????????' \
			'Name=state,Values=available' --region us-east-1 --output json | \
			jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId'"
    ).trim(),
    bfdServerAmiId: sh(
      returnStdout: true,
      script: "/usr/local/bin/aws ec2 describe-images --owners self --filters \
			'Name=name,Values=bfd-fhir-??????????????' \
			'Name=state,Values=available' --region us-east-1 --output json | \
			jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId'"
    ).trim(),
  )
}

/**
 * <p>
 * Builds the base "platinum" AMI to use for new instances/AMIs.
 * </p>
 * <p>
 * Why "platinum"? Because it's essentially a respin of the CCS environment's "gold master", with
 * updates and additional tooling and configuration applied.
 * </p>
 *
 * @param amiIds an {@link AmiIds} instance detailing the IDs of the AMIs that already exist
 * @return a new {@link AmiIds} instance detailing the shiny new AMI that is now available for use, and any other that were already available
 * @throws RuntimeException An exception will be bubbled up if the AMI-builder tooling returns a non-zero exit code.
 */
def buildPlatinumAmi(AmiIds amiIds) {
	withCredentials([file(credentialsId: 'bluebutton-ansible-playbooks-data-ansible-vault-password', variable: 'vaultPasswordFile')]) {
		def goldAmi = sh(
			returnStdout: true,
			script: "/usr/local/bin/aws ec2 describe-images --filters \
			'Name=name,Values=\"EAST-RH 7-6 Gold Image V.1.10 (HVM) ??-??-??\"' \
			'Name=state,Values=available' --region us-east-1 --output json | \
			jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId'"
			).trim()

		// packer is always run from $repoRoot/ops/ansible/playbooks-ccs
		dir('ops/ansible/playbooks-ccs'){
			sh "/usr/bin/packer build -color=false -var vault_password_file=${vaultPasswordFile} \
			-var source_ami=${goldAmi} \
			-var subnet_id=subnet-06e6736253a5e5eda \
			../../packer/build_bfd-platinum.json"
		}
	  return new AmiIds(
			platinumAmiId: extractAmiIdFromPackerManifest(new File("${workspace}/ops/ansible/playbooks-ccs/manifest_platinum.json")),
			bfdPipelineAmiId: amiIds.bfdPipelineAmiId, 
			bfdServerAmiId: amiIds.bfdServerAmiId,
		)
 	}
}

/**
 * Deploys/redeploys Jenkins and related systems to the management environment.
 *
 * @param amiIds an {@link AmiIds} instance detailing the IDs of the AMIs that should be used
 * @throws RuntimeException An exception will be bubbled up if the deploy tooling returns a non-zero exit code.
 */
def deployManagement(AmiIds amiIds) {
	echo 'Deploy to the CCS mgmt environment is not yet implemented.'
	throw new UnsupportedOperationException('Deploy to the CCS mgmt environment is not yet implemented.')
}

/**
 * Builds the BFD Pipeline and BFD Server AMIs.
 *
 * @param environmentId The name of the environment for which the AMIs are being built
 * @param amiIds an {@link AmiIds} instance detailing the IDs of the AMIs that already exist
 * @param appBuildResults the {@link AppBuildResults} containing the paths to the app binaries that were built
 * @return a new {@link AmiIds} instance detailing the shiny new AMIs that are now available for use
 * @throws RuntimeException An exception will be bubbled up if the AMI-builder tooling returns a non-zero exit code.
 */
def buildAppAmis(String environmentId, AmiIds amiIds, AppBuildResults appBuildResults) {
	dir('ops/ansible/playbooks-ccs'){
		withCredentials([file(credentialsId: 'bluebutton-ansible-playbooks-data-ansible-vault-password', variable: 'vaultPasswordFile')]) {

			// both packer builds expect additional variables in a file called `extra_vars.json` in the current directory
		
			def varsFile = new File("${workspace}/ops/ansible/playbooks-ccs/extra_vars.json")
			def fhirWar = new File(appBuildResults.dataServerWar)
			def appServer = new File(appBuildResults.dataServerContainerZip)

			varsFile.write(JsonOutput.toJson([
				env: normalizeEnvironmentId(environmentId),
				data_server_war_local_dir: "${workspace}/${fhirWar.getParent()}",
				data_server_war: "${workspace}/${fhirWar.getParent()}/${fhirWar.getName()}",
				data_server_appserver_local_dir: "${workspace}/${appServer.getParent()}",
				data_server_container: "${workspace}/${fhirWar.getParent()}/server-work/${appServer.getName()}",
				data_server_container_name: appBuildResults.dataServerContainerName,
				data_pipeline_jar: "${workspace}/${appBuildResults.dataPipelineUberJar}",
			]))
			 
			// build the ETL pipeline
			
			sh "/usr/bin/packer build -color=false \
				-var vault_password_file=${vaultPasswordFile} \
				-var 'source_ami=${amiIds.platinumAmiId}' \
				-var 'subnet_id=subnet-06e6736253a5e5eda' \
				../../packer/build_bfd-pipeline.json"

			// build the FHIR server
			sh "/usr/bin/packer build -color=false \
				-var vault_password_file=${vaultPasswordFile} \
				-var 'source_ami=${amiIds.platinumAmiId}' \
				-var 'subnet_id=subnet-06e6736253a5e5eda' \
				../../packer/build_bfd-server.json"

			return new AmiIds(
				platinumAmiId: amiIds.platinumAmiId,
				bfdPipelineAmiId: extractAmiIdFromPackerManifest(new File(
					"${workspace}/ops/ansible/playbooks-ccs/manifest_data-pipeline.json")),
				bfdServerAmiId: extractAmiIdFromPackerManifest(new File(
					"${workspace}/ops/ansible/playbooks-ccs/manifest_data-server.json")),
			)
		}
	}
}

/**
 * Deploys to the specified environment.
 *
 * @param envId the ID of the environment to deploy to
 * @param amiIds an {@link AmiIds} instance detailing the IDs of the AMIs that should be used
 * @param appBuildResults (not used in the CCS environment; this stuff is all baked into the AMIs there, instead)
 * @throws RuntimeException An exception will be bubbled up if the deploy tooling returns a non-zero exit code.
 */
def deploy(String environmentId, AmiIds amiIds, AppBuildResults appBuildResults) {
	def env = normalizeEnvironmentId(environmentId)
		dir("${workspace}/ops/terraform/env/${env}/stateless") {

		// Debug output terraform version 
		sh "/usr/bin/terraform --version"
		
		// Initilize terraform 
		sh "/usr/bin/terraform init"
		
		// Gathering terraform plan 
		sh "/usr/bin/terraform plan \
		-var='fhir_ami=${amiIds.bfdServerAmiId}' \
		-var='etl_ami=${amiIds.bfdPipelineAmiId}' \
		-var='ssh_key_name=bfd-${env}' \
		-no-color -out=tfplan"
		
		// Apply Terraform plan
		sh "/usr/bin/terraform apply \
		-no-color -input=false tfplan"
	}
}


def extractAmiIdFromPackerManifest(File manifest) {
	dir('ops/ansible/playbooks-ccs'){
		def manifestJson = new JsonSlurper().parseText(manifest.text)

		// artifactId will be of the form $region:$amiId
		return manifestJson.builds[0].artifact_id.split(":")[1]
	}
}

def normalizeEnvironmentId(String environmentId) {
	switch (environmentId) {
		case 'test':
		case 'prod':
		case 'prod-sbx':
			return environmentId
		case 'prod-stg':
			return 'prod-sbx'
		default:
			throw new UnsupportedOperationException("Deploy to the CCS ${environmentId} environment is not yet implemented.")
  }
}

return this