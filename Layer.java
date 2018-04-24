//
//  Layer
//
//
//  Created by Gauraang Khurana on 3/24/18.
//
//
import java.util.ArrayList;

class Layer {

		public ArrayList<String> preconditionList;
		public ArrayList<String> effectList;
		public String actionName;

		public Layer() {
			preconditionList = new ArrayList<>();
			effectList = new ArrayList<>();
		}
		
		public String getName() {
			return actionName;
		}

		public void setName(String name) {
			this.actionName = name;
		}

	}