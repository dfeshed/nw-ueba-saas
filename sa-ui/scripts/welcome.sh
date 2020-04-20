# Use this script if you need the entire environment set up
# Though it is not be harmful to run this regardless
# what state your environment is in.

scriptDir="$(dirname "$0")"

. $scriptDir/_util.sh

. $scriptDir/local-env/nuke.sh

info "Now setting up environment and apps..."
info "Get comfy, this might take awhile!\n"
info "Performing initial setup"

. $scriptDir/local-env/initial-setup.sh

info "Installing apps\n"

. $scriptDir/local-env/app-setup.sh

success "Congrats, you are all set up!\n"
success "IMPORTANT: To use your new environment, please open up a new terminal window.\n"
success "Then 'cd' into an app, any app, and run 'ember serve' to get started.\n"