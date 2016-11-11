########################################################
# WARNING: This is not ready to use.
########################################################
elifePipeline {
    stage 'Checkout'
    checkout scm
    def commit = elifeGitRevision()

    stage 'Project tests'
    lock('search--ci') {
        builderDeployRevision 'search--ci', commit
        builderProjectTests 'search--ci', '/srv/silex-starter', ['/srv/silex-starter/build/phpunit.xml']
    }

    elifeMainlineOnly {
        stage 'End2end tests'
        elifeEnd2EndTest({
            builderDeployRevision 'silex-starter--end2end', commit
            builderSmokeTests 'silex-starter--end2end', '/srv/silex-starter'
        }, 'two')

        stage 'Approval'
        elifeGitMoveToBranch commit, 'approved'

        stage 'Not production yet'
        elifeGitMoveToBranch commit, 'master'
    }
}
