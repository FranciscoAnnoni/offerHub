import requests
import base64
from geopy.geocoders import Nominatim
from geopy.exc import GeocoderTimedOut, GeocoderUnavailable
import time


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

    latitud = "Error de geolocalización"
    longitud = "Error de geolocalización"

    return latitud, longitud

    """direccion=direccion.lower().replace("avenida ","").replace("av. ","")
    geolocator = Nominatim(user_agent="mi_app_de_geolocalizacion", domain="nominatim.openstreetmap.org")
    try:
        location = geolocator.geocode(direccion, country_codes="AR")

        if location:
            latitud = str(location.latitude)
            longitud = str(location.longitude)
        else:
            latitud = "No posee coordenadas"
            longitud = "No posee coordenadas"
            
    except (GeocoderTimedOut, GeocoderUnavailable):
        latitud = "Error de geolocalización"
        longitud = "Error de geolocalización"

    return latitud, longitud"""
