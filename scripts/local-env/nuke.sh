CWD=$(pwd)

scriptDir="$(dirname "$0")"
. $scriptDir/_util.sh

red $scriptDir

red "Nuking your environment from orbit..."
red "...it's the only way to be sure.\n"

info "If this is your first time setting up environment, this part will be fast."

function nukeNode {
  info "Removing $1 node_modules"
  rm -rf $scriptDir/../$1/node_modules
}

info "Cleaning NPM cache"
npm cache clean

rm -rf $scriptDir/../node_modules
touch node_modules/.metadata_never_index

nukeNode mock-server
nukeNode component-lib
nukeNode streaming-data
nukeNode packager
nukeNode recon
nukeNode style-guide
nukeNode investigate-events
nukeNode respond
nukeNode context
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
info "Now setting up environment and apps..."

cd $CWD