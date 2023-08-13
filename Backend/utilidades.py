import requests
import base64
from geopy.geocoders import Nominatim

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

def obtenerCoordenadas(direccion):
    geolocator = Nominatim(user_agent="mi_app_de_geolocalizacion")
    
    try:
        location = geolocator.geocode(direccion, country_codes="AR")
        if location:
            latitud = location.latitude
            longitud = location.longitude
            return latitud, longitud
        else:
            return None
    except Exception as e:
        print("Error:", e)
        return None
