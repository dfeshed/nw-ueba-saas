CWD=$(pwd)

scriptDir="$(dirname "$0")"
cd $scriptDir

. _util.sh

red $scriptDir

red "Nuking your environment from orbit..."
red "...it's the only way to be sure.\n"

info "If this is your first time setting up environment, this part will be fast."

function nukeApp {
  info "Removing $1 node_modules"
  rm -rf ../$1/node_modules
  mkdir ../$1/node_modules
  touch ../$1/node_modules/.metadata_never_index
  info "Removing $1 bower_components"
  rm -rf ../$1/bower_components
  mkdir ../$1/bower_components
  touch ../$1/bower_components/.metadata_never_index
}

info "Cleaning NPM cache"
npm cache clean
info "Cleaning Bower cache"
bower cache clean

nukeApp component-lib
nukeApp streaming-data
nukeApp recon
nukeApp style-guide
nukeApp sa

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