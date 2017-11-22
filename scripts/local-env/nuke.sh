CWD=$(pwd)

scriptDir="$(dirname "$0")"
. $scriptDir/_util.sh

red $scriptDir

red "Nuking your environment from orbit..."
red "...it's the only way to be sure.\n"

function nukeNode {
  info "Removing $1 node_modules"
  unlink $scriptDir/../$1/node_modules

  # keeping this around to handle cases where
  # bouncing between 11.0 and 11.1 and node_modules
  # is an actual directory
  rm -rf $scriptDir/../$1/node_modules
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
nukeNode investigate-events
nukeNode investigate-hosts
nukeNode investigate-files
nukeNode respond
nukeNode configure
nukeNode context
nukeNode preferences
nukeNode test-helpers
nukeNode broccoli-theme
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
