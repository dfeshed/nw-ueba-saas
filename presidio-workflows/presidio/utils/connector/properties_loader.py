def load_properties(filepath, sep='=', comment_char='#'):
    """
    Read the file passed as parameter as a properties file.
    Credit Roberto@stackoverflow
    See https://stackoverflow.com/questions/3595363/properties-file-in-python-similar-to-java-properties
    """
    props = {}
    with open(filepath, "rt") as f:
        for line in f:
            l = line.strip()
            if l and not l.startswith(comment_char):
                key_value = l.split(sep)
                key = key_value[0].strip()
                value = sep.join(key_value[1:]).strip().strip('"')
                props[key] = value
    return props


def load_and_get_property(property_to_get, filepath, sep='=', comment_char='#'):
    """
    Read the file passed as parameter as a properties file and return the value of property 'property_to_get'.
    """
    property_to_get = property_to_get.replace(":", "\\:")  # java-props escapes char ':' and python just reads it as string
    return load_properties(filepath, sep, comment_char)[property_to_get]


