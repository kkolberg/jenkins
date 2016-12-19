def command = [ "/bin/bash", "-c", "git ls-remote --tags '${repo}' | awk '{print \$2}' | grep -v '{}' | awk -F'/' '{print \$3}'" ]
def process = command.execute(); process.waitFor()
def result = process.in.text.tokenize("\n") 

