/* eslint-disable no-undef */
module.exports = {
  greetings(name) {
    console.log(`Worker: received argument: "${name}"`);//eslint-disable-line
    console.log(`Worker: returning message: "Hello ${name}"`);//eslint-disable-line
    return `Hello ${name}`;
  }
};