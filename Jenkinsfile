elifePipeline {
    def commit
    stage 'Checkout', {
        checkout scm
        commit = elifeGitRevision()
    }

    stage 'Project tests', {
        lock('schematron-validator--ci') {
            builderDeployRevision 'schematron-validator--ci', commit
            builderProjectTests 'schematron-validator--ci', '/srv/schematron-validator'
        }
    }
    
    elifeMainlineOnly {
        stage 'Approval', {
            elifeGitMoveToBranch commit, 'approved'
        }
    }
}
