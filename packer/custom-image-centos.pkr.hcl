packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

source "googlecompute" "centos-image" {
  project_id            = var.project_id
  source_image          = var.source_image
  image_name            = "${var.image_name}-{{timestamp}}"
  zone                  = var.zone
  disk_size             = var.disk_size
  network               = var.network
  communicator          = var.communicator
  ssh_username          = var.ssh_username
  ssh_password          = var.ssh_password
  ssh_timeout           = var.ssh_timeout
  service_account_email = var.service_account_email
}

build {
  sources = ["sources.googlecompute.centos-image"]

  provisioner "shell" {
    script = "./scripts/create_user.sh"
  }

  provisioner "shell" {
    script = "./scripts/install_java.sh"
  }

  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/"
  }

  provisioner "shell" {
    script = "./scripts/transfer_ownership.sh"
  }

  provisioner "file" {
    source      = "./csye6225.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    script = "./scripts/start_sysD.sh"
  }

  provisioner "shell" {
    script = "./scripts/install_ops_agent.sh"
  }

  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
  }
}
