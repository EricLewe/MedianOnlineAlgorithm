import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.json.*;

public class Main {
	/**
	 * An iterative online algorithm for calculating the median exchange rate 
	 * for USD to SEK during last year (that is 2016).
	 * 
	 * The calculated median will be outputed in "standard output stream".
	 * 
	 * @param args Not used argument
	 * @throws IOException Will be thrown in case fetching data from Modular finances API does not go as planned. 
	 */
	public static void main(String[] args) throws IOException {
		URL url = new URL("http://fx.modfin.se/2016-01-01/2016-12-99?base=usd&symbols=usd,sek");
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		// Parse the JSON, and convert it to a JSONArray
		JSONTokener jsonTokener = new JSONTokener(is);
		JSONArray jsonObj = new JSONArray(jsonTokener);
		Iterator<Object> jsonIterator = jsonObj.iterator();
		ArrayList<Double> input = new ArrayList<Double>();

		PriorityQueue<Double> minPQ = new PriorityQueue<Double>();
		PriorityQueue<Double> maxPQ = new PriorityQueue<Double>(10, new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				return -o1.compareTo(o2);
			}
		});

		for (int i = 0; jsonIterator.hasNext(); i++) {
			JSONObject tempobj = new JSONObject(jsonIterator.next().toString());
			tempobj = tempobj.getJSONObject("rates");
			
			Double currentRate = new Double((double)tempobj.getInt("SEK"));
			input.add(currentRate);

			//online algorithm using a maximum and minimum heap
			if (i == 1) {
				minPQ.add(Math.max(input.get(0), input.get(1)));
				maxPQ.add(Math.min(input.get(0), input.get(1)));
			} else if (1 < i) {
				//Step 1
				if (currentRate < maxPQ.peek()) {
					maxPQ.add(currentRate);
				} else {
					minPQ.add(currentRate);
				}
				//Step 2, balance heaps
				if (minPQ.size() < maxPQ.size()) {
					minPQ.add(maxPQ.poll());
				} else if (maxPQ.size() < minPQ.size()) {
					maxPQ.add(minPQ.poll());
				}
			}
		}
		
		double result = -1;
		
		if (minPQ.size() == maxPQ.size()) {
			result = (double)(minPQ.peek() + maxPQ.peek()) / 2;
		} else {
			int max = Math.max(maxPQ.size(), minPQ.size());
			if (max == minPQ.size()) {
				result = minPQ.peek();
			}
			if (max == maxPQ.size()) {
				result = maxPQ.peek();
			}
		}
		System.out.println("Median: " + result);
	}
}
