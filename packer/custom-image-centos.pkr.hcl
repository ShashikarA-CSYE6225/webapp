packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

source "googlecompute" "centos-image-example" {
  project_id            = "csye6225-dev-414805"
  source_image          = "centos-stream-8-v20240110"
  image_name            = "centos-webapp"
  zone                  = "us-east4-a"
  disk_size             = 20
  network               = "default"
  communicator          = "ssh"
  ssh_username          = "packer"
  ssh_password          = "root"
  ssh_timeout           = "1h"
  credentials_file      = "csye6225-dev-414805-024b357000b2.json"
  service_account_email = "centoscustomimage@csye6225-dev-414805.iam.gserviceaccount.com"
}

build {
  sources = ["sources.googlecompute.centos-image-example"]

  provisioner "file" {
    source      = "scripts/create_user.sh"
    destination = "/tmp/create_user.sh"
  }

  provisioner "shell" {
    inline = [
      "chmod +x /tmp/create_user.sh",
      "sudo /tmp/create_user.sh"
    ]
  }

  provisioner "file" {
    source      = "scripts/install_java_sql.sh"
    destination = "/tmp/install_java_sql.sh"
  }

  provisioner "shell" {
    inline = [
      "chmod +x /tmp/install_java_sql.sh",
      "sudo /tmp/install_java_sql.sh",
    ]
  }

  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/webapp-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "scripts/transfer_ownership.sh"
    destination = "/tmp/transfer_ownership.sh"
  }

  provisioner "shell" {
    inline = [
      "chmod +x /tmp/transfer_ownership.sh",
      "sudo /tmp/transfer_ownership.sh",
    ]
  }

  #  provisioner "file" {
  #    source      = "scripts/install_maven.sh"
  #    destination = "/tmp/install_maven.sh"
  #  }
  #
  #  provisioner "shell" {
  #    inline = [
  #      "chmod +x /tmp/install_maven.sh",
  #      "sudo /tmp/install_maven.sh",
  #    ]
  #  }

  #  provisioner "file" {
  #    source      = "csye6225.service"
  #    destination = "/etc/systemd/system/csye6225.service"
  #  }
  #
  #  provisioner "shell" {
  #    inline = [
  #      "chmod 644 /etc/systemd/system/csye6225.service",
  #      "systemctl daemon-reload",
  #      "systemctl enable csye6225"
  #    ]
  #  }
}
