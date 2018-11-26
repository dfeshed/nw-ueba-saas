import userDetails from './user_details';

const urlMap = [{
  url: '/details',
  data: userDetails
}];

export default (req) => {
  return urlMap.find(({ url }) => req.indexOf(url) > 0) ? urlMap.find(({ url }) => req.indexOf(url) > 0).data : userDetails;
};