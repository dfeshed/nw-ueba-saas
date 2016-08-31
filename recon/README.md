# Recon

The recon addon can be used by defining just one component in your template:

```hbs
{{recon-container
  endpointId=endpointId
  eventId=eventId
  meta=model.meta
  title=title}}
```

### endpointId

### eventId

### meta
`meta` is the array of meta to display for meta details

### title
`title` is the title to display, i.e. `Event Reconstruction (3 of 2567)`
This is optional and will display just `Event Reconstruction`, if you pass nothing.
