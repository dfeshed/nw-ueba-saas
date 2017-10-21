import Ember from 'ember';
const { Helper } = Ember;

export function signatureText([value, signer]) {
  let signatureValue = value;
  if (!value) {
    signatureValue = 'unsigned';
  } else if (signer) {
    signatureValue = `${value},${signer}`;
  }
  return signatureValue;
}

export default Helper.helper(signatureText);
