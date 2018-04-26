CWD=$(pwd)

scriptDir="$(dirname "$0")"
. $scriptDir/_util.sh

red $scriptDir

red "Nuking your environment from orbit..."
red "...it's the only way to be sure.\n"

function nukeNode {
  info "Removing $1 node_modules"
  # use if actual (not linked) node_modules directory
  # gets into folder while doing script work
  # rm -rf $scriptDir/../$1/node_modules

  unlink $scriptDir/../$1/node_modules

  # remove any pesky error logs hanging around
  # while doing build script work
  # rm $scriptDir/../$1/yarn-error.log
}

rm -rf $scriptDir/../node_modules
touch node_modules/.metadata_never_index

nukeNode mock-server
nukeNode component-lib
nukeNode streaming-data
nukeNode packager
nukeNode hosts-scan-configure
nukeNode recon
nukeNode style-guide
nukeNode investigate-shared
nukeNode investigate-events
nukeNode investigate-hosts
nukeNode investigate-files
nukeNode respond
nukeNode configure
nukeNode admin-source-management
nukeNode admin
nukeNode context
nukeNode preferences
nukeNode test-helpers
nukeNode ember-route-action-helper
nukeNode broccoli-theme
nukeNode investigate-process-analysis
nukeNode sa

red "                               ________________
                          ____/ (  (    )   )  \___
                          ( (  (  )   _    ))  )   )
                       ((     (   )(    )  )   (   )  )
                     ((/  ( _(   )   (   _) ) (  () )  )
                    ( (  ( (_)   ((    (   )  .((_ ) .  )_
                   ( (  )    (      (  )    )   ) . ) (   )
                  (  (   (  (   ) (  _  ( _) ).  ) . ) ) ( )
                  ( (  (   ) (  )   (  ))     ) _)(   )  )  )
                 ( (  ( \ ) (    (_  ( ) ( )  )   ) )  )) ( )
                  (  (   (  (   (_ ( ) ( _    )  ) (  )  )   )
                 ( (  ( (  (  )     (_  )  ) )  _)   ) _( ( )
                  ((  (   )(    (     _    )   _) _(_ (  (_ )
                   (_((__(_(__(( ( ( |  ) ) ) )_))__))_)___)
                   ((__)        \\||lll|l||///          \_))
                              ( /(/ (  )  ) )\ )
                            (  ( ( ( | | ) ) )\  )
                             ( /(| / ( )) ) ) )) )
                           (   ( ((((_(|)_)))))   )
                            (    ||\(|(|)|/||   )
                          (      |(||(||)||||      )
                            (   //|/l|||)|\\ \   )
                        (/ / //  /|//||||\\  \ \  \ _)
-------------------------------------------------------------------------------"


red "You've been NUKED!"

cd $CWD
