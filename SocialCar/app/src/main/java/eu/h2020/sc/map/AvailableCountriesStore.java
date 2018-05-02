package eu.h2020.sc.map;

import com.google.android.gms.maps.model.LatLng;
import eu.h2020.sc.domain.Country;
import eu.h2020.sc.domain.TimeZoneIDConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pietro on 16/09/16.
 */
public class AvailableCountriesStore {

    private static AvailableCountriesStore instance;
    private Map<String, Country> countries;

    private AvailableCountriesStore() {
        this.countries = new HashMap<>();
        this.putAllSupportedCountry();
    }

    public static AvailableCountriesStore getInstance() {
        if (instance == null)
            instance = new AvailableCountriesStore();
        return instance;
    }

    public Country retrieveCountryFromTimeZone(String timeZone) {
        return this.countries.get(timeZone);
    }

    private void putAllSupportedCountry() {
        this.countries.put(TimeZoneIDConstant.AMSTERDAM, new Country(TimeZoneIDConstant.AMSTERDAM, new LatLng(52.157160, 5.388903)));
        this.countries.put(TimeZoneIDConstant.ATHENS, new Country(TimeZoneIDConstant.ATHENS, new LatLng(38.062043, 23.637397)));
        this.countries.put(TimeZoneIDConstant.VIENNA, new Country(TimeZoneIDConstant.VIENNA, new LatLng(52.157160, 5.388903)));
        this.countries.put(TimeZoneIDConstant.ZURICH, new Country(TimeZoneIDConstant.ZURICH, new LatLng(46.873961, 8.431565)));
        this.countries.put(TimeZoneIDConstant.BELGRADE, new Country(TimeZoneIDConstant.BELGRADE, new LatLng(44.387471, 20.753149)));
        this.countries.put(TimeZoneIDConstant.BERLIN, new Country(TimeZoneIDConstant.BERLIN, new LatLng(51.147233, 10.495390)));
        this.countries.put(TimeZoneIDConstant.BRUSSELS, new Country(TimeZoneIDConstant.BRUSSELS, new LatLng(50.757051, 4.784015)));
        this.countries.put(TimeZoneIDConstant.BUDAPEST, new Country(TimeZoneIDConstant.BUDAPEST, new LatLng(47.057009, 19.471429)));
        this.countries.put(TimeZoneIDConstant.COPENHAGEN, new Country(TimeZoneIDConstant.COPENHAGEN, new LatLng(56.018700, 10.256240)));
        this.countries.put(TimeZoneIDConstant.DUBLIN, new Country(TimeZoneIDConstant.DUBLIN, new LatLng(53.170104, -8.025466)));
        this.countries.put(TimeZoneIDConstant.HELSINKI, new Country(TimeZoneIDConstant.HELSINKI, new LatLng(64.888229, 27.420007)));
        this.countries.put(TimeZoneIDConstant.LISBON, new Country(TimeZoneIDConstant.LISBON, new LatLng(9.789257, -8.152072)));
        this.countries.put(TimeZoneIDConstant.LJUBLJANA, new Country(TimeZoneIDConstant.LJUBLJANA, new LatLng(46.101824, 14.892231)));
        this.countries.put(TimeZoneIDConstant.LONDON, new Country(TimeZoneIDConstant.LONDON, new LatLng(52.753351, -1.272160)));
        this.countries.put(TimeZoneIDConstant.LUXEMBOURG, new Country(TimeZoneIDConstant.LUXEMBOURG, new LatLng(49.742245, 6.108576)));
        this.countries.put(TimeZoneIDConstant.MADRID, new Country(TimeZoneIDConstant.MADRID, new LatLng(40.598160, -3.426801)));
        this.countries.put(TimeZoneIDConstant.NICOSIA, new Country(TimeZoneIDConstant.NICOSIA, new LatLng(35.061235, 33.343099)));
        this.countries.put(TimeZoneIDConstant.OSLO, new Country(TimeZoneIDConstant.OSLO, new LatLng(66.341052, 13.568824)));
        this.countries.put(TimeZoneIDConstant.PARIS, new Country(TimeZoneIDConstant.PARIS, new LatLng(47.228329, 2.753743)));
        this.countries.put(TimeZoneIDConstant.PRAGUE, new Country(TimeZoneIDConstant.PRAGUE, new LatLng(49.824670, 15.682438)));
        this.countries.put(TimeZoneIDConstant.ROME, new Country(TimeZoneIDConstant.ROME, new LatLng(41.994006, 12.875154)));
        this.countries.put(TimeZoneIDConstant.SOFIA, new Country(TimeZoneIDConstant.SOFIA, new LatLng(42.743814, 25.470705)));
        this.countries.put(TimeZoneIDConstant.STOCKHOLM, new Country(TimeZoneIDConstant.STOCKHOLM, new LatLng(62.905465, 16.264446)));
        this.countries.put(TimeZoneIDConstant.ZAGREB, new Country(TimeZoneIDConstant.ZAGREB, new LatLng(44.770206, 16.390451)));
        this.countries.put(TimeZoneIDConstant.SKOPJE, new Country(TimeZoneIDConstant.SKOPJE, new LatLng(41.650369, 21.741168)));
    }

}
