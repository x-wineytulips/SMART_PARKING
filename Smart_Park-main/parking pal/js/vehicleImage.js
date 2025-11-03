class VehicleImageHandler {
    constructor() {
        this.setupImageUpload();
        this.setupImagePreview();
    }

    setupImageUpload() {
        const uploadForm = document.getElementById('vehicleImageForm');
        if (uploadForm) {
            uploadForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const formData = new FormData(uploadForm);
                await this.uploadImage(formData);
            });
        }
    }

    async uploadImage(formData) {
        try {
            const ticketId = document.getElementById('ticketId').value;
            const response = await fetch(`/api/parking/images/upload/${ticketId}`, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const data = await response.json();
                this.addImageToPreview(data.url);
                this.showSuccessMessage('Image uploaded successfully');
            } else {
                throw new Error('Failed to upload image');
            }
        } catch (error) {
            this.showErrorMessage('Failed to upload image: ' + error.message);
        }
    }

    addImageToPreview(imageUrl) {
        const previewContainer = document.getElementById('imagePreview');
        const imageElement = document.createElement('div');
        imageElement.className = 'vehicle-image-preview';
        imageElement.innerHTML = `
            <img src="${imageUrl}" alt="Vehicle Image">
            <div class="image-overlay">
                <span class="timestamp">${new Date().toLocaleTimeString()}</span>
            </div>
        `;
        previewContainer.appendChild(imageElement);
    }

    setupImagePreview() {
        const input = document.getElementById('vehicleImage');
        if (input) {
            input.addEventListener('change', (e) => {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        const preview = document.getElementById('imagePreview');
                        preview.style.backgroundImage = `url(${e.target.result})`;
                    };
                    reader.readAsDataURL(file);
                }
            });
        }
    }
} 