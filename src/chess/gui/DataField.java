package chess.gui;

public enum DataField {
	BOARDS_EXPANDED {
		@Override
		public String toString() {
			return "Boards generated";
		}
	}, TIME {
		@Override
		public String toString() {
			return "Time (ms)";
		}
	}, BOARDS_PER_MS {
		@Override
		public String toString() {
			return "Boards/ms";
		}
	};
	
	abstract public String toString();
}
