# Use this script if you need the entire environment set up
# Though it should not be harmful to run this regardless
# how set up your environment is

CWD=$(pwd)
scriptDir="$(dirname "$0")"
cd $scriptDir
source util.sh

info "Get comfy, this might take awhile!\n"
info "Performing initial setup"

./initial-setup.sh

info "Installing apps\n"

./app-setup.sh

success "Congrats, you are all set up!\n"
success "IMPORTANT: To use your new environment, please open up a new terminal window.\n"
success "Then 'cd' into an app, any app, and run 'ember serve' to get started.\n"

cd $CWD