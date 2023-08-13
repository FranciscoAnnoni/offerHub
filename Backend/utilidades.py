import requests
import base64

def imagenABase64(urlImagen):
    try:
        response = requests.get(urlImagen)
        if response.status_code == 200:
            image_data = response.content
            base64_data = base64.b64encode(image_data).decode('utf-8')
            return base64_data
        else:
            return None
    except Exception as e:
        return None
