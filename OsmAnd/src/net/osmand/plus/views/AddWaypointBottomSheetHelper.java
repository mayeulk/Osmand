package net.osmand.plus.views;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.GPXUtilities;
import net.osmand.plus.GPXUtilities.GPXFile;
import net.osmand.plus.GPXUtilities.NewGpxWaypoint;
import net.osmand.plus.IconsCache;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.mapcontextmenu.MapContextMenu;

public class AddWaypointBottomSheetHelper {
	private final View view;
	private final TextView title;
	private String titleText = "";
	private final TextView description;
	private final Context context;
	private final MapContextMenu contextMenu;
	private final ContextMenuLayer contextMenuLayer;
	private boolean applyingPositionMode;
	private NewGpxWaypoint newGpxWaypoint;

	public AddWaypointBottomSheetHelper(final MapActivity activity, ContextMenuLayer ctxMenuLayer) {
		this.contextMenuLayer = ctxMenuLayer;
		view = activity.findViewById(R.id.add_gpx_waypoint_bottom_sheet);
		title = (TextView) view.findViewById(R.id.add_gpx_waypoint_bottom_sheet_title);
		description = (TextView) view.findViewById(R.id.description);
		context = activity;
		contextMenu = activity.getContextMenu();
		ImageView icon = (ImageView) view.findViewById(R.id.icon);

		IconsCache iconsCache = activity.getMyApplication().getIconsCache();
		icon.setImageDrawable(iconsCache.getIcon(R.drawable.ic_action_photo_dark, R.color.marker_green));
		view.findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				contextMenuLayer.createGpxWaypoint();
				GPXFile gpx = newGpxWaypoint.getGpx();
				LatLon latLon = contextMenu.getLatLon();
				activity.getContextMenu().getWptPtPointEditor().add(gpx, latLon, titleText);
			}
		});
		view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				contextMenuLayer.cancelAddGpxWaypoint();
			}
		});
	}

	public void onDraw(RotatedTileBox rt) {
		PointF point = contextMenuLayer.getMovableCenterPoint(rt);
		double lat = rt.getLatFromPixel(point.x, point.y);
		double lon = rt.getLonFromPixel(point.x, point.y);
		description.setText(PointDescription.getLocationName(context, lat, lon, true));
	}

	public void setTitle(String title) {
		titleText = title;
		this.title.setText(titleText);
	}

	public boolean isVisible() {
		return view.getVisibility() == View.VISIBLE;
	}

	public void show(Drawable drawable, NewGpxWaypoint newGpxWaypoint) {
		this.newGpxWaypoint = newGpxWaypoint;
		exitApplyPositionMode();
		view.setVisibility(View.VISIBLE);
		((ImageView) view.findViewById(R.id.icon)).setImageDrawable(drawable);
	}

	public void hide() {
		exitApplyPositionMode();
		view.setVisibility(View.GONE);
	}

	public void enterApplyPositionMode() {
		if (!applyingPositionMode) {
			applyingPositionMode = true;
			view.findViewById(R.id.create_button).setEnabled(false);
		}
	}

	public void exitApplyPositionMode() {
		if (applyingPositionMode) {
			applyingPositionMode = false;
			view.findViewById(R.id.create_button).setEnabled(true);
		}
	}
}